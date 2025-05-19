import axios from 'axios';

const baseApiUrl = 'api/ai';

export default class AkipAiService {
  public sendMessage(userMessage: any, conversationId: string): Promise<any> {
    return axios.post(`${baseApiUrl}/chat/${conversationId}`, userMessage, { headers: { 'Content-Type': 'application/json' } });
  }

  public getConversations(): Promise<any> {
    return axios.post(`${baseApiUrl}/chat/conversations`);
  }

  public getMessages(conversationId: string): Promise<any> {
    return axios.get(`${baseApiUrl}/chat/${conversationId}`);
  }
}
