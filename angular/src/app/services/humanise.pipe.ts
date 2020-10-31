import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'humanise'
})
export class HumanisePipe implements PipeTransform {

  transform(value: string, ...args: unknown[]): unknown {
    if ((typeof value) !== 'string') {
      return value;
    }
    value = value.split(/(?=[A-Z])/).join(' ');
    value = value[0].toUpperCase() + value.slice(1);
    return value;
  }

}
