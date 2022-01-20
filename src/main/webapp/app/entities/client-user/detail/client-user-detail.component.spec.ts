import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ClientUserDetailComponent } from './client-user-detail.component';

describe('ClientUser Management Detail Component', () => {
  let comp: ClientUserDetailComponent;
  let fixture: ComponentFixture<ClientUserDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ClientUserDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ clientUser: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ClientUserDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ClientUserDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load clientUser on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.clientUser).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
