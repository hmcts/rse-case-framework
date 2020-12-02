import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfirmServiceComponent } from './confirm-service.component';
import {HttpClientModule} from "@angular/common/http";
import {RouterModule} from "@angular/router";

describe('ConfirmServiceComponent', () => {
  let component: ConfirmServiceComponent;
  let fixture: ComponentFixture<ConfirmServiceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ConfirmServiceComponent ],
      imports: [
        RouterModule.forRoot([], { relativeLinkResolution: 'legacy' })
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfirmServiceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
