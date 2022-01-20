import dayjs from 'dayjs/esm';
import { IInspection } from 'app/entities/inspection/inspection.model';
import { IVisit } from 'app/entities/visit/visit.model';
import { IClientUser } from 'app/entities/client-user/client-user.model';
import { IEngineer } from 'app/entities/engineer/engineer.model';
import { IAddress } from 'app/entities/address/address.model';
import { Priority } from 'app/entities/enumerations/priority.model';

export interface IJob {
  id?: number;
  priority?: Priority | null;
  created?: dayjs.Dayjs | null;
  occupiersName?: string | null;
  occupiersHomePhone?: string | null;
  occupiersWorkPhone?: string | null;
  occupiersMobilePhone?: string | null;
  clientOrderId?: string | null;
  assignedAt?: dayjs.Dayjs | null;
  scheduled?: dayjs.Dayjs | null;
  completed?: dayjs.Dayjs | null;
  invoiceNumber?: string | null;
  fault?: string | null;
  accessInstructions?: string | null;
  updated?: dayjs.Dayjs | null;
  inspections?: IInspection[] | null;
  visits?: IVisit[] | null;
  createdBy?: IClientUser | null;
  assignedTo?: IEngineer | null;
  address?: IAddress | null;
}

export class Job implements IJob {
  constructor(
    public id?: number,
    public priority?: Priority | null,
    public created?: dayjs.Dayjs | null,
    public occupiersName?: string | null,
    public occupiersHomePhone?: string | null,
    public occupiersWorkPhone?: string | null,
    public occupiersMobilePhone?: string | null,
    public clientOrderId?: string | null,
    public assignedAt?: dayjs.Dayjs | null,
    public scheduled?: dayjs.Dayjs | null,
    public completed?: dayjs.Dayjs | null,
    public invoiceNumber?: string | null,
    public fault?: string | null,
    public accessInstructions?: string | null,
    public updated?: dayjs.Dayjs | null,
    public inspections?: IInspection[] | null,
    public visits?: IVisit[] | null,
    public createdBy?: IClientUser | null,
    public assignedTo?: IEngineer | null,
    public address?: IAddress | null
  ) {}
}

export function getJobIdentifier(job: IJob): number | undefined {
  return job.id;
}
