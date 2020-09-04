import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateCaseComponent } from './create-case.component';
import { HttpClientModule } from '@angular/common/http';
import { RouterModule } from '@angular/router';

describe('CreateCaseComponent', () => {
  let component: CreateCaseComponent;
  let fixture: ComponentFixture<CreateCaseComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CreateCaseComponent ],
      imports: [
        RouterModule.forRoot([]),
        HttpClientModule
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CreateCaseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
