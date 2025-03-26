import { type ISearch } from '@/shared/model/search.model';

export interface IComment {
  id?: number;
  body?: string | null;
  sentiment?: number | null;
  author?: string | null;
  createDate?: Date | null;
  search?: ISearch | null;
}

export class Comment implements IComment {
  constructor(
    public id?: number,
    public body?: string | null,
    public sentiment?: number | null,
    public author?: string | null,
    public createDate?: Date | null,
    public search?: ISearch | null,
  ) {}
}
