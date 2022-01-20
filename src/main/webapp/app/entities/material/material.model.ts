import { IVisit } from 'app/entities/visit/visit.model';

export interface IMaterial {
  id?: number;
  name?: string | null;
  description?: string | null;
  quantity?: number | null;
  unitCost?: number | null;
  visit?: IVisit | null;
}

export class Material implements IMaterial {
  constructor(
    public id?: number,
    public name?: string | null,
    public description?: string | null,
    public quantity?: number | null,
    public unitCost?: number | null,
    public visit?: IVisit | null
  ) {}
}

export function getMaterialIdentifier(material: IMaterial): number | undefined {
  return material.id;
}
