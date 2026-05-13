import axios from 'axios';

const baseApiUrl = 'api/comments-collector';

export interface CollectResponse {
  comments: any[];
  warnings: string[];
}

export default class CommentsCollectorService {
  public async retrieveCommentApi(payload: { urls: string[]; keyword: string | null; search: string | null }): Promise<CollectResponse> {
    const res = await axios.post<CollectResponse>(`${baseApiUrl}/collect`, payload);
    return res.data;
  }
}
