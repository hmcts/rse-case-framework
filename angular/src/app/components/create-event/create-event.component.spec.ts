import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateEventComponent } from './create-event.component';
import {RouterModule} from '@angular/router';
import {HttpClientModule} from '@angular/common/http';

describe('CreateEventComponent', () => {
  let component: CreateEventComponent;
  let fixture: ComponentFixture<CreateEventComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CreateEventComponent ],
      imports: [
        RouterModule.forRoot([]),
        HttpClientModule
      ],
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
