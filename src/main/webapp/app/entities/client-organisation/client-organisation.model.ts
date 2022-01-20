import { IClientUser } from 'app/entities/client-user/client-user.model';

export interface IClientOrganisation {
  id?: number;
  name?: string | null;
  domain?: string | null;
  clientUsers?: IClientUser[] | null;
}

export class ClientOrganisation implements IClientOrganisation {
  constructor(public id?: number, public name?: string | null, public domain?: string | null, public clientUsers?: IClientUser[] | null) {}
}

export function getClientOrganisationIdentifier(clientOrganisation: IClientOrganisation): number | undefined {
  return clientOrganisation.id;
}
