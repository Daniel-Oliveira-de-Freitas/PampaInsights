import { type ISearch } from '@/shared/model/search.model';

import { type Visualization } from '@/shared/model/enumerations/visualization.model';
import { type TypeOfChart } from '@/shared/model/enumerations/type-of-chart.model';
import { type Emotions } from '@/shared/model/enumerations/emotions.model';
import type { SentimentAnalysisType } from '@/shared/model/enumerations/sentiment-analysis-type.model.ts';
export interface IFilter {
  id?: number;
  sentimentAnalysisType?: SentimentAnalysisType | null;
  visualization?: keyof typeof Visualization | null;
  typeOfChart?: keyof typeof TypeOfChart | null;
  emotions?: keyof typeof Emotions | null;
  search?: ISearch | null;
}

export class Filter implements IFilter {
  constructor(
    public id?: number,
    public sentimentAnalysisType?: SentimentAnalysisType | null,
    public visualization?: keyof typeof Visualization | null,
    public typeOfChart?: keyof typeof TypeOfChart | null,
    public emotions?: keyof typeof Emotions | null,
    public search?: ISearch | null,
  ) {}
}
