export interface IMessage {
  role?: string | null;
  content?: string | null;
}

export class Message implements IMessage {
  constructor(
    public role?: string | null,
    public content?: string | null,
  ) {}
}
