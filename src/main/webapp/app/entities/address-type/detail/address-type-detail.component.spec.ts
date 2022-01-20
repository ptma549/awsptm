import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AddressTypeDetailComponent } from './address-type-detail.component';

describe('AddressType Management Detail Component', () => {
  let comp: AddressTypeDetailComponent;
  let fixture: ComponentFixture<AddressTypeDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AddressTypeDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ addressType: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(AddressTypeDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(AddressTypeDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load addressType on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.addressType).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
