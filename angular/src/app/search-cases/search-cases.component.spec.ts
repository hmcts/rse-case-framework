import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SearchCasesComponent } from './search-cases.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { RouterModule } from '@angular/router';

describe('SearchCasesComponent', () => {
  let component: SearchCasesComponent;
  let fixture: ComponentFixture<SearchCasesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
        FormsModule,
        HttpClientModule,
        RouterModule.forRoot([]),
    ],
      declarations: [ SearchCasesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SearchCasesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
