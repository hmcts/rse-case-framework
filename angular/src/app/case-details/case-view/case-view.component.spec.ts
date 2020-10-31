import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CaseViewComponent } from './case-view.component';
import {ActivatedRoute, RouterModule} from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import {Observable, of} from "rxjs";

describe('CaseViewComponent', () => {
  let component: CaseViewComponent;
  let fixture: ComponentFixture<CaseViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterModule.forRoot([]),
        HttpClientModule,
      ],
      declarations: [ CaseViewComponent ],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            paramMap: of({ get: (key) => '1' }),
            queryParamMap: of({ get: (key) => '1' })
          }
        }
      ]
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
