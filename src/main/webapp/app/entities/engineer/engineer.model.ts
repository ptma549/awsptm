import { IUser } from 'app/entities/user/user.model';
import { IJob } from 'app/entities/job/job.model';

export interface IEngineer {
  id?: number;
  firstname?: string | null;
  lastname?: string | null;
  email?: string | null;
  user?: IUser | null;
  jobs?: IJob[] | null;
}

export class Engineer implements IEngineer {
  constructor(
    public id?: number,
    public firstname?: string | null,
    public lastname?: string | null,
    public email?: string | null,
    public user?: IUser | null,
    public jobs?: IJob[] | null
  ) {}
}

export function getEngineerIdentifier(engineer: IEngineer): number | undefined {
  return engineer.id;
}
