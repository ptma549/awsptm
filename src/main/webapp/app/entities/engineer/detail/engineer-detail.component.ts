import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IEngineer } from '../engineer.model';

@Component({
  selector: 'jhi-engineer-detail',
  templateUrl: './engineer-detail.component.html',
})
export class EngineerDetailComponent implements OnInit {
  engineer: IEngineer | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ engineer }) => {
      this.engineer = engineer;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
