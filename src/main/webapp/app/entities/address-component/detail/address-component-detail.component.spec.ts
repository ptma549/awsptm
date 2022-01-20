import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AddressComponentDetailComponent } from './address-component-detail.component';

describe('AddressComponent Management Detail Component', () => {
  let comp: AddressComponentDetailComponent;
  let fixture: ComponentFixture<AddressComponentDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AddressComponentDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ addressComponent: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(AddressComponentDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(AddressComponentDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load addressComponent on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.addressComponent).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
