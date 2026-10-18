/** Types échangés avec le back : ils reprennent les records Java du dossier dto/. */

export type Role = 'CLIENT' | 'AGENT';

export interface User {
  id: number;
  displayName: string;
  role: Role;
}

export interface Conversation {
  id: number;
  clientId: number;
  clientName: string;
  createdAt: string;
}

export interface ChatMessage {
  id: number;
  conversationId: number;
  authorId: number;
  authorName: string;
  authorRole: Role;
  content: string;
  sentAt: string;
}
