import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClaimsTabComponent } from './claims-tab.component';
import {HttpClientModule} from "@angular/common/http";

describe('ClaimsTabComponent', () => {
  let component: ClaimsTabComponent;
  let fixture: ComponentFixture<ClaimsTabComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        HttpClientModule
      ],
      declarations: [ ClaimsTabComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ClaimsTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
