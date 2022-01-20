import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IEvidence } from '../evidence.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-evidence-detail',
  templateUrl: './evidence-detail.component.html',
})
export class EvidenceDetailComponent implements OnInit {
  evidence: IEvidence | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ evidence }) => {
      this.evidence = evidence;
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  previousState(): void {
    window.history.back();
  }
}
