export interface ISearch {
  id?: number;
  name?: string | null;
  favorite?: boolean | null;
  createDate?: Date | null;
}

export class Search implements ISearch {
  constructor(
    public id?: number,
    public name?: string | null,
    public favorite?: boolean | null,
    public createDate?: Date | null,
  ) {
    this.favorite = this.favorite ?? false;
  }
}
