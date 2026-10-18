import { DatePipe } from '@angular/common';
import {
  Component,
  ElementRef,
  Injector,
  afterNextRender,
  afterRenderEffect,
  computed,
  effect,
  inject,
  signal,
  viewChild,
} from '@angular/core';
import { FormField, form, maxLength, required, submit } from '@angular/forms/signals';
import { ChatService } from './chat.service';
import { ChatMessage, Conversation, User } from './models';

/**
 * L'unique écran de la PoC, en trois temps :
 * 1. choisir qui l'on est (le client ou l'agent de démonstration) ;
 * 2. choisir une conversation (le client peut aussi en ouvrir une) ;
 * 3. lire l'historique et échanger en direct.
 *
 * Tout l'état est fait de signals : l'écran se met à jour quand l'un d'eux change.
 */
@Component({
  selector: 'app-root',
  imports: [DatePipe, FormField],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  protected readonly chat = inject(ChatService);
  private readonly injector = inject(Injector);

  // --- État ---

  protected readonly user = signal<User | null>(null);
  protected readonly selected = signal<Conversation | null>(null);
  /** Messages reçus en direct depuis l'ouverture de la conversation. */
  private readonly live = signal<ChatMessage[]>([]);
  /** Texte lu par les lecteurs d'écran (zone <output>), sans déplacer le focus. */
  protected readonly notice = signal('');

  // --- Lectures REST : relancées automatiquement quand user() ou selected() change ---

  protected readonly users = this.chat.users();
  protected readonly conversations = this.chat.conversations(this.user);
  private readonly history = this.chat.history(this.selected);

  /** Historique + messages en direct, sans doublon, dans l'ordre d'envoi. */
  protected readonly messages = computed(() => {
    const byId = new Map<number, ChatMessage>();
    [...this.history.value(), ...this.live()].forEach((m) => byId.set(m.id, m));
    return [...byId.values()].sort((a, b) => a.id - b.id);
  });

  // --- Abonnements STOMP : onCleanup désabonne quand le signal lu change ---

  /** L'agent reçoit les nouvelles conversations des clients. */
  private readonly newConversations = effect((onCleanup) => {
    if (this.user()?.role !== 'AGENT') {
      return;
    }
    onCleanup(
      this.chat.subscribe<Conversation>('/topic/conversations', (conversation) => {
        this.conversations.value.update((list) => [conversation, ...list]);
        this.notice.set(
          `Nouvelle conversation n° ${conversation.id} de ${conversation.clientName}.`,
        );
      }),
    );
  });

  /** Messages en direct de la conversation affichée. */
  private readonly liveMessages = effect((onCleanup) => {
    const conversation = this.selected();
    if (!conversation) {
      return;
    }
    onCleanup(
      this.chat.subscribe<ChatMessage>(`/topic/conversations/${conversation.id}`, (message) =>
        this.live.update((list) => [...list, message]),
      ),
    );
  });

  // --- Formulaire d'envoi (Signal Forms) ---

  protected readonly messageForm = form(signal({ content: '' }), (path) => {
    required(path.content, { message: 'Écrivez un message avant de l’envoyer.' });
    maxLength(path.content, 2000, { message: 'Le message ne doit pas dépasser 2 000 caractères.' });
  });

  // --- Éléments de la page ---

  private readonly whoTitle = viewChild<ElementRef<HTMLElement>>('whoTitle');
  private readonly messageInput = viewChild<ElementRef<HTMLTextAreaElement>>('messageInput');
  private readonly messageLog = viewChild<ElementRef<HTMLElement>>('messageLog');

  /** Après chaque nouveau message, fait défiler la zone des messages jusqu'en bas. */
  private readonly autoScroll = afterRenderEffect(() => {
    this.messages();
    const log = this.messageLog()?.nativeElement;
    if (log) {
      log.scrollTop = log.scrollHeight;
    }
  });

  // --- Actions ---

  protected chooseUser(user: User): void {
    this.user.set(user);
    this.notice.set('');
  }

  protected changeUser(): void {
    this.user.set(null);
    this.selected.set(null);
    this.notice.set('');
    // Le focus revient sur le titre « Qui êtes-vous ? » une fois celui-ci affiché.
    this.afterRender(() => this.whoTitle()?.nativeElement.focus());
  }

  protected openConversation(): void {
    const user = this.user();
    if (user) {
      this.chat.openConversation(user.id).subscribe((conversation) => {
        this.conversations.value.update((list) => [conversation, ...list]);
        this.select(conversation);
        this.notice.set(
          `Conversation n° ${conversation.id} ouverte. Un conseiller va vous répondre.`,
        );
      });
    }
  }

  protected select(conversation: Conversation): void {
    this.live.set([]);
    this.selected.set(conversation);
    this.afterRender(() => this.messageInput()?.nativeElement.focus());
  }

  /** Entrée envoie le message, Maj+Entrée va à la ligne. */
  protected onKeydown(event: KeyboardEvent): void {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.send();
    }
  }

  protected send(): void {
    // submit() vérifie les règles du formulaire et n'appelle action que si elles sont respectées.
    void submit(this.messageForm, {
      action: (field) => {
        const conversation = this.selected();
        const user = this.user();
        if (conversation && user && this.chat.connected()) {
          this.chat.sendMessage(conversation.id, user.id, field.content().value().trim());
          field().reset({ content: '' });
        }
        // L'envoi STOMP n'attend pas de réponse : la soumission est terminée tout de suite.
        return Promise.resolve(undefined);
      },
    });
  }

  /** Exécute une action après la prochaine mise à jour de l'écran (l'élément existe alors). */
  private afterRender(action: () => void): void {
    afterNextRender(action, { injector: this.injector });
  }
}
