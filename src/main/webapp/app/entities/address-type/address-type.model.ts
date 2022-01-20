import { IAddressComponent } from 'app/entities/address-component/address-component.model';

export interface IAddressType {
  id?: number;
  name?: string | null;
  position?: number | null;
  addressComponents?: IAddressComponent[] | null;
}

export class AddressType implements IAddressType {
  constructor(
    public id?: number,
    public name?: string | null,
    public position?: number | null,
    public addressComponents?: IAddressComponent[] | null
  ) {}
}

export function getAddressTypeIdentifier(addressType: IAddressType): number | undefined {
  return addressType.id;
}
