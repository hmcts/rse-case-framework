import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CitizenTabComponent } from './citizen-tab.component';
import {HttpClientModule} from "@angular/common/http";
import {RouterModule} from "@angular/router";

describe('CitizenTabComponent', () => {
  let component: CitizenTabComponent;
  let fixture: ComponentFixture<CitizenTabComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CitizenTabComponent ],
      imports: [
        HttpClientModule,
        RouterModule.forRoot([], { relativeLinkResolution: 'legacy' })
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CitizenTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
