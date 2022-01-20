import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ClientOrganisationDetailComponent } from './client-organisation-detail.component';

describe('ClientOrganisation Management Detail Component', () => {
  let comp: ClientOrganisationDetailComponent;
  let fixture: ComponentFixture<ClientOrganisationDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ClientOrganisationDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ clientOrganisation: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ClientOrganisationDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ClientOrganisationDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load clientOrganisation on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.clientOrganisation).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
