import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { SearchCasesComponent } from './search-cases.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { RouterModule } from '@angular/router';
import {BASE_PATH} from "../../generated/client-lib";
import {environment} from "../../environments/environment";

describe('SearchCasesComponent', () => {
  let component: SearchCasesComponent;
  let fixture: ComponentFixture<SearchCasesComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
        FormsModule,
        HttpClientModule,
        RouterModule.forRoot([], { relativeLinkResolution: 'legacy' }),
    ],
      providers: [
        { provide: BASE_PATH, useValue: environment.baseUrl },
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
