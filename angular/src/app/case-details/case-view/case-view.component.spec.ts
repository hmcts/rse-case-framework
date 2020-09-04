import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CaseViewComponent } from './case-view.component';
import { RouterModule } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';

describe('CaseViewComponent', () => {
  let component: CaseViewComponent;
  let fixture: ComponentFixture<CaseViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterModule.forRoot([]),
        HttpClientModule,
      ],
      declarations: [ CaseViewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CaseViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
