import { TestBed } from '@angular/core/testing';

import { CaseService } from './case-service.service';
import { HttpClientModule } from '@angular/common/http';
import {environment} from "../../environments/environment";
import {BASE_PATH} from "../../generated/client-lib";

describe('CaseServiceService', () => {
  let service: CaseService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
    ],
      providers: [
        { provide: BASE_PATH, useValue: environment.baseUrl }
        ],

    });
    service = TestBed.inject(CaseService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
