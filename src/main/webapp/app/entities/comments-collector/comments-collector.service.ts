import axios from 'axios';

const baseApiUrl = 'api/comments-collector';

export default class CommentsCollectorService {
  public async retrieveCommentApi(payload: { urls: string[]; keyword: string | null }): Promise<any> {
    return new Promise<any>((resolve, reject) => {
      axios
        .post(`${baseApiUrl}/collect`, payload)
        .then(res => {
          resolve(res.data);
          console.log('res', res.data);
        })
        .catch(err => {
          console.log('err', err);
          reject(err);
        });
    });
  }
}
