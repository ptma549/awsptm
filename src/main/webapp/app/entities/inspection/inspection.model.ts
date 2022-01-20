import dayjs from 'dayjs/esm';
import { IJob } from 'app/entities/job/job.model';
import { IClientUser } from 'app/entities/client-user/client-user.model';
import { IAddress } from 'app/entities/address/address.model';
import { Priority } from 'app/entities/enumerations/priority.model';

export interface IInspection {
  id?: number;
  priority?: Priority | null;
  created?: dayjs.Dayjs | null;
  occupiersName?: string | null;
  occupiersHomePhone?: string | null;
  occupiersWorkPhone?: string | null;
  occupiersMobilePhone?: string | null;
  work?: string | null;
  accessInstructions?: string | null;
  updated?: dayjs.Dayjs | null;
  start?: dayjs.Dayjs | null;
  frequency?: string | null;
  jobs?: IJob | null;
  createdBy?: IClientUser | null;
  address?: IAddress | null;
}

export class Inspection implements IInspection {
  constructor(
    public id?: number,
    public priority?: Priority | null,
    public created?: dayjs.Dayjs | null,
    public occupiersName?: string | null,
    public occupiersHomePhone?: string | null,
    public occupiersWorkPhone?: string | null,
    public occupiersMobilePhone?: string | null,
    public work?: string | null,
    public accessInstructions?: string | null,
    public updated?: dayjs.Dayjs | null,
    public start?: dayjs.Dayjs | null,
    public frequency?: string | null,
    public jobs?: IJob | null,
    public createdBy?: IClientUser | null,
    public address?: IAddress | null
  ) {}
}

export function getInspectionIdentifier(inspection: IInspection): number | undefined {
  return inspection.id;
}
