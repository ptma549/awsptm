import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IClientUser } from '../client-user.model';

@Component({
  selector: 'jhi-client-user-detail',
  templateUrl: './client-user-detail.component.html',
})
export class ClientUserDetailComponent implements OnInit {
  clientUser: IClientUser | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ clientUser }) => {
      this.clientUser = clientUser;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
