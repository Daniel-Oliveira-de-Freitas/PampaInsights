import axios from 'axios';

const baseApiUrl = 'api/ai';

export default class AkipAiService {
  public async streamMessage(userMessage: any, conversationId: string, onToken: (token: string) => void): Promise<void> {
    const authToken = localStorage.getItem('jhi-authenticationToken') || sessionStorage.getItem('jhi-authenticationToken');
    const response = await fetch(`${baseApiUrl}/chat/${conversationId}/stream`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Accept: 'text/event-stream',
        ...(authToken ? { Authorization: `Bearer ${authToken}` } : {}),
      },
      body: JSON.stringify(userMessage),
    });

    if (!response.ok || !response.body) {
      throw new Error(`Stream request failed: ${response.status}`);
    }

    const reader = response.body.getReader();
    const decoder = new TextDecoder();
    let buffer = '';

    for (;;) {
      const { value, done } = await reader.read();
      if (done) {
        break;
      }
      buffer += decoder.decode(value, { stream: true });

      let sepIndex: number;
      while ((sepIndex = buffer.indexOf('\n\n')) !== -1) {
        const rawEvent = buffer.slice(0, sepIndex);
        buffer = buffer.slice(sepIndex + 2);

        const data = rawEvent
          .split('\n')
          .filter(line => line.startsWith('data:'))
          .map(line => line.slice(5))
          .join('\n');

        if (data) {
          onToken(data);
        }
      }
    }
  }

  public createConversation(): Promise<any> {
    return axios.post(`${baseApiUrl}/chat/create-conversation`);
  }

  public getConversations(): Promise<any> {
    return axios.post(`${baseApiUrl}/chat/conversations`);
  }

  public getMessages(conversationId: string): Promise<any> {
    return axios.get(`${baseApiUrl}/chat/${conversationId}`);
  }
}
