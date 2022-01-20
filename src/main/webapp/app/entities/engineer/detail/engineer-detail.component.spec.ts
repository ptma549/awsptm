import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { EngineerDetailComponent } from './engineer-detail.component';

describe('Engineer Management Detail Component', () => {
  let comp: EngineerDetailComponent;
  let fixture: ComponentFixture<EngineerDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [EngineerDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ engineer: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(EngineerDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(EngineerDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load engineer on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.engineer).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
