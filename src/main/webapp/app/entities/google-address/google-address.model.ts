import { IAddressComponent } from 'app/entities/address-component/address-component.model';
import { IAddress } from 'app/entities/address/address.model';

export interface IGoogleAddress {
  id?: number;
  position?: string | null;
  url?: string | null;
  html?: string | null;
  formatted?: string | null;
  types?: string | null;
  addressComponents?: IAddressComponent[] | null;
  address?: IAddress | null;
}

export class GoogleAddress implements IGoogleAddress {
  constructor(
    public id?: number,
    public position?: string | null,
    public url?: string | null,
    public html?: string | null,
    public formatted?: string | null,
    public types?: string | null,
    public addressComponents?: IAddressComponent[] | null,
    public address?: IAddress | null
  ) {}
}

export function getGoogleAddressIdentifier(googleAddress: IGoogleAddress): number | undefined {
  return googleAddress.id;
}
