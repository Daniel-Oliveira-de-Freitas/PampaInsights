import { type ISearch } from '@/shared/model/search.model';

export interface IParameter {
  id?: number;
  terms?: string | null;
  webSite?: string | null;
  maxPages?: number | null;
  instagram?: string | null;
  facebook?: string | null;
  linkedin?: string | null;
  x?: string | null;
  createDate?: Date | null;
  search?: ISearch | null;
}

export class Parameter implements IParameter {
  constructor(
    public id?: number,
    public terms?: string | null,
    public webSite?: string | null,
    public maxPages?: number | null,
    public instagram?: string | null,
    public facebook?: string | null,
    public linkedin?: string | null,
    public x?: string | null,
    public createDate?: Date | null,
    public search?: ISearch | null,
  ) {}
}
