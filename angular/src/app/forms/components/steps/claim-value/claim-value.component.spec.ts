import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClaimValueComponent } from './claim-value.component';

describe('ClaimValueComponent', () => {
  let component: ClaimValueComponent;
  let fixture: ComponentFixture<ClaimValueComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ClaimValueComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ClaimValueComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
