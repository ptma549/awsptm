import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { GoogleAddressDetailComponent } from './google-address-detail.component';

describe('GoogleAddress Management Detail Component', () => {
  let comp: GoogleAddressDetailComponent;
  let fixture: ComponentFixture<GoogleAddressDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GoogleAddressDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ googleAddress: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(GoogleAddressDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(GoogleAddressDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load googleAddress on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.googleAddress).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
