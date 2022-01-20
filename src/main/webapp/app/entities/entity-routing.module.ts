import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'inspection',
        data: { pageTitle: 'Inspections' },
        loadChildren: () => import('./inspection/inspection.module').then(m => m.InspectionModule),
      },
      {
        path: 'job',
        data: { pageTitle: 'Jobs' },
        loadChildren: () => import('./job/job.module').then(m => m.JobModule),
      },
      {
        path: 'visit',
        data: { pageTitle: 'Visits' },
        loadChildren: () => import('./visit/visit.module').then(m => m.VisitModule),
      },
      {
        path: 'certificate',
        data: { pageTitle: 'Certificates' },
        loadChildren: () => import('./certificate/certificate.module').then(m => m.CertificateModule),
      },
      {
        path: 'evidence',
        data: { pageTitle: 'Evidences' },
        loadChildren: () => import('./evidence/evidence.module').then(m => m.EvidenceModule),
      },
      {
        path: 'material',
        data: { pageTitle: 'Materials' },
        loadChildren: () => import('./material/material.module').then(m => m.MaterialModule),
      },
      {
        path: 'client-user',
        data: { pageTitle: 'ClientUsers' },
        loadChildren: () => import('./client-user/client-user.module').then(m => m.ClientUserModule),
      },
      {
        path: 'client-organisation',
        data: { pageTitle: 'ClientOrganisations' },
        loadChildren: () => import('./client-organisation/client-organisation.module').then(m => m.ClientOrganisationModule),
      },
      {
        path: 'engineer',
        data: { pageTitle: 'Engineers' },
        loadChildren: () => import('./engineer/engineer.module').then(m => m.EngineerModule),
      },
      {
        path: 'address',
        data: { pageTitle: 'Addresses' },
        loadChildren: () => import('./address/address.module').then(m => m.AddressModule),
      },
      {
        path: 'google-address',
        data: { pageTitle: 'GoogleAddresses' },
        loadChildren: () => import('./google-address/google-address.module').then(m => m.GoogleAddressModule),
      },
      {
        path: 'address-component',
        data: { pageTitle: 'AddressComponents' },
        loadChildren: () => import('./address-component/address-component.module').then(m => m.AddressComponentModule),
      },
      {
        path: 'address-type',
        data: { pageTitle: 'AddressTypes' },
        loadChildren: () => import('./address-type/address-type.module').then(m => m.AddressTypeModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
