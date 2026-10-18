import { HttpClient, httpResource } from '@angular/common/http';
import { DestroyRef, Injectable, Signal, inject, signal } from '@angular/core';
import { Client, StompSubscription } from '@stomp/stompjs';
import { Observable } from 'rxjs';
import { ChatMessage, Conversation, User } from './models';

/**
 * Tous les échanges avec le back.
 *
 * - REST : les lectures sont des `httpResource`, des signals qui lancent la requête HTTP et
 *   la relancent (en annulant la précédente) quand un signal dont ils dépendent change.
 * - STOMP sur WebSocket : l'envoi et la réception des messages en direct.
 */
@Injectable({ providedIn: 'root' })
export class ChatService {
  private readonly http = inject(HttpClient);

  /** Vrai quand la WebSocket est ouverte : sans elle, on ne peut pas envoyer de message. */
  readonly connected = signal(false);

  /** Abonnements en cours, refaits automatiquement après une reconnexion. */
  private readonly handlers = new Map<string, (body: unknown) => void>();
  private readonly subscriptions = new Map<string, StompSubscription>();

  private readonly stomp = new Client({
    // Le proxy d'Angular (proxy.conf.json) redirige /ws vers le back sur le port 8080.
    brokerURL: `ws://${location.host}/ws`,
    reconnectDelay: 3000,
    onConnect: () => {
      this.connected.set(true);
      this.handlers.forEach((handler, destination) => this.subscribeNow(destination, handler));
    },
    onWebSocketClose: () => {
      this.connected.set(false);
      this.subscriptions.clear();
    },
  });

  /** La connexion vit aussi longtemps que le service : ouverte à sa création, fermée à sa fin. */
  constructor() {
    this.stomp.activate();
    inject(DestroyRef).onDestroy(() => void this.stomp.deactivate());
  }

  // --- REST : lectures (à appeler dans un champ de composant) ---

  users() {
    return httpResource<User[]>(() => '/api/users', { defaultValue: [] });
  }

  /** Conversations de l'utilisateur ; aucune requête tant qu'il n'est pas choisi. */
  conversations(user: Signal<User | null>) {
    return httpResource<Conversation[]>(
      () => {
        const current = user();
        return current ? { url: '/api/conversations', params: { userId: current.id } } : undefined;
      },
      { defaultValue: [] },
    );
  }

  /** Historique de la conversation choisie ; aucune requête tant qu'il n'y en a pas. */
  history(conversation: Signal<Conversation | null>) {
    return httpResource<ChatMessage[]>(
      () => {
        const current = conversation();
        return current ? `/api/conversations/${current.id}/messages` : undefined;
      },
      { defaultValue: [] },
    );
  }

  // --- REST : action ---

  /** Une seule réponse : HttpClient termine l'Observable ensuite, rien à désabonner. */
  openConversation(clientId: number): Observable<Conversation> {
    return this.http.post<Conversation>('/api/conversations', { clientId });
  }

  // --- STOMP ---

  /**
   * S'abonne à une destination et renvoie la fonction qui annule l'abonnement.
   * Le composant la passe à `onCleanup` dans un `effect` : le désabonnement est automatique.
   */
  subscribe<T>(destination: string, handler: (body: T) => void): () => void {
    const untyped = handler as (body: unknown) => void;
    this.handlers.set(destination, untyped);
    if (this.stomp.connected) {
      this.subscribeNow(destination, untyped);
    }
    return () => {
      this.handlers.delete(destination);
      this.subscriptions.get(destination)?.unsubscribe();
      this.subscriptions.delete(destination);
    };
  }

  sendMessage(conversationId: number, authorId: number, content: string): void {
    this.stomp.publish({
      destination: `/app/conversations/${conversationId}/messages`,
      body: JSON.stringify({ authorId, content }),
    });
  }

  private subscribeNow(destination: string, handler: (body: unknown) => void): void {
    this.subscriptions.get(destination)?.unsubscribe();
    this.subscriptions.set(
      destination,
      this.stomp.subscribe(destination, (frame) => handler(JSON.parse(frame.body))),
    );
  }
}
