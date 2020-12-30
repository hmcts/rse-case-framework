import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { CreateEventComponent } from './create-event.component';
import {ActivatedRoute, RouterModule} from '@angular/router';
import {HttpClientModule} from '@angular/common/http';
import {of} from 'rxjs';

describe('CreateEventComponent', () => {
  let component: CreateEventComponent;
  let fixture: ComponentFixture<CreateEventComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ CreateEventComponent ],
      imports: [
        RouterModule.forRoot([], { relativeLinkResolution: 'legacy' }),
        HttpClientModule
      ],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            paramMap: of({ get: (key) => 'CreateClaim' }),
            queryParamMap: of({ get: (key) => 'CreateClaim' })
          }
        }
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CreateEventComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
