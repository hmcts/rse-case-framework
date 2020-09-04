import { TestBed } from '@angular/core/testing';

import { CaseService } from './case-service.service';
import { HttpClient, HttpClientModule } from '@angular/common/http';

describe('CaseServiceService', () => {
  let service: CaseService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
    ],
    });
    service = TestBed.inject(CaseService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
