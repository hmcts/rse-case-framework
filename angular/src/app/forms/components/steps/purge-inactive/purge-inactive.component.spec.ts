import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PurgeInactiveComponent } from './purge-inactive.component';
import {RouterModule} from "@angular/router";
import {HttpClientModule} from "@angular/common/http";

describe('PurgeInactiveComponent', () => {
  let component: PurgeInactiveComponent;
  let fixture: ComponentFixture<PurgeInactiveComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterModule.forRoot([], { relativeLinkResolution: 'legacy' }),
        HttpClientModule,
      ],
      declarations: [ PurgeInactiveComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PurgeInactiveComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
