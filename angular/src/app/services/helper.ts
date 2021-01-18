
export class Utils {
  static notNull<T>(val: T | undefined | null): T {
    if (!val) {
      throw new Error('Undefined value');
    }
    return val;
  }

  static caseId(val: string | undefined | null): string {
    return this.notNull(val).split('-').join('');
  }
}
