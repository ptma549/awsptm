import { IGoogleAddress } from 'app/entities/google-address/google-address.model';
import { IAddressType } from 'app/entities/address-type/address-type.model';

export interface IAddressComponent {
  id?: number;
  longName?: string | null;
  shortName?: string | null;
  address?: IGoogleAddress | null;
  type?: IAddressType | null;
}

export class AddressComponent implements IAddressComponent {
  constructor(
    public id?: number,
    public longName?: string | null,
    public shortName?: string | null,
    public address?: IGoogleAddress | null,
    public type?: IAddressType | null
  ) {}
}

export function getAddressComponentIdentifier(addressComponent: IAddressComponent): number | undefined {
  return addressComponent.id;
}
