import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AnswerSectionComponent } from './answer-section.component';

describe('AnswerSectionComponent', () => {
  let component: AnswerSectionComponent;
  let fixture: ComponentFixture<AnswerSectionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AnswerSectionComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AnswerSectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
