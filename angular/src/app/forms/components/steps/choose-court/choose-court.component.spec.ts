import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ChooseCourtComponent } from './choose-court.component';

describe('ChooseCourtComponent', () => {
  let component: ChooseCourtComponent;
  let fixture: ComponentFixture<ChooseCourtComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ ChooseCourtComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ChooseCourtComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
