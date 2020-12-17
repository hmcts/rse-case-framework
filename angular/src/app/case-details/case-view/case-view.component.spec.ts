import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { CaseViewComponent } from './case-view.component';
import {ActivatedRoute, RouterModule} from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import {of} from 'rxjs';
import {environment} from '../../../environments/environment';
import {BASE_PATH} from '../../../generated/client-lib';

describe('CaseViewComponent', () => {
  let component: CaseViewComponent;
  let fixture: ComponentFixture<CaseViewComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterModule.forRoot([], { relativeLinkResolution: 'legacy' }),
        HttpClientModule,
      ],
      declarations: [ CaseViewComponent ],
      providers: [
        { provide: BASE_PATH, useValue: environment.baseUrl },
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
