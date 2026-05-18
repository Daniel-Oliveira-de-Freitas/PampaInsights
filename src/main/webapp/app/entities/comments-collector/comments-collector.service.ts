import axios from 'axios';

const baseApiUrl = 'api/comments-collector';

export interface CollectResponse {
  comments: any[];
}

export default class CommentsCollectorService {
  public async retrieveCommentApi(payload: { urls: string[]; keyword: string | null; search: string | null }): Promise<CollectResponse> {
    const res = await axios.post<CollectResponse>(`${baseApiUrl}/collect`, payload);
    return res.data;
  }
}
