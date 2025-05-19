import type { IMessage } from '@/shared/model/message.model';
import { Message } from '@/shared/model/message.model';

export interface IChatMessage {
  message?: IMessage | null;
  conversationId?: string | null;
}

export class ChatMessage implements IChatMessage {
  constructor(
    public message?: Message | null,
    public conversationId?: string | null,
  ) {}
}
