import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PartiesTabComponent } from './parties-tab.component';

describe('Parties tab', () => {
  let component: PartiesTabComponent;
  let fixture: ComponentFixture<PartiesTabComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
        imports: [
        ],
      declarations: [ PartiesTabComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PartiesTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
