const CFG = { FORM: 'Control Panel', DB: 'Tire Database' };

function onOpen() {
  SpreadsheetApp.getUi().createMenu('Tire Shop Tools')
    .addItem('Find Tire', 'findTire')
    .addItem('Add Tire', 'addTire')
    .addItem('Update Tire', 'updateTire')
    .addItem('Clear Form', 'clearForm')
    .addToUi();
}

function sheets_() {
  const ss = SpreadsheetApp.getActiveSpreadsheet();
  const form = ss.getSheetByName(CFG.FORM);
  const db = ss.getSheetByName(CFG.DB);
  if (!form || !db) throw new Error('Keep the sheets named Control Panel and Tire Database.');
  return {ss, form, db};
}

const FIELDS = ['No','Diameter','Height','Width','Brand','Purchase Price','Made In','Selling Price','Car Name','Purchase Date','Selling Date','Quantity','Status','Notes'];

function readForm_() {
  const v = sheets_().form.getRange('B5:B19').getValues().flat();
  return Object.fromEntries(FIELDS.map((k,i) => [k, v[i] || '']));
}

function writeForm_(r) {
  sheets_().form.getRange('B5:B19').setValues(FIELDS.map(k => [r[k] || '']));
}

function map_(db) {
  const h = db.getRange(1,1,1,db.getLastColumn()).getValues()[0];
  const m = {}; h.forEach((x,i) => { if (x !== '') m[String(x).trim()] = i+1; });
  return m;
}

function makeRow_(r,m,n) {
  const a = Array(n).fill('');
  Object.keys(r).forEach(k => { if (m[k]) a[m[k]-1] = r[k]; });
  return a;
}

function findTire() {
  const {ss,form,db} = sheets_();
  const id = String(form.getRange('B5').getDisplayValue()).trim();
  if (!id) return SpreadsheetApp.getUi().alert('Enter Tire No / ID in B5.');
  const m = map_(db), last = db.getLastRow();
  if (last < 2) return SpreadsheetApp.getUi().alert('No records found.');
  const data = db.getRange(2,1,last-1,db.getLastColumn()).getValues();
  const i = data.findIndex(row => String(row[m['No']-1]).trim() === id);
  if (i < 0) return SpreadsheetApp.getUi().alert('Tire ID not found: ' + id);
  const r = {}; Object.keys(m).forEach(k => r[k] = data[i][m[k]-1]);
  writeForm_(r); ss.setActiveSheet(form);
  SpreadsheetApp.getUi().alert('Record found. Edit yellow cells, then choose Update Tire.');
}

function addTire() {
  const {db} = sheets_(), r = readForm_(), m = map_(db), id = String(r.No).trim();
  if (!id) return SpreadsheetApp.getUi().alert('Enter a unique Tire No / ID in B5.');
  if (db.getLastRow() >= 2) {
    const ids = db.getRange(2,m.No,db.getLastRow()-1,1).getDisplayValues().flat();
    if (ids.includes(id)) return SpreadsheetApp.getUi().alert('ID already exists. Use Find, then Update.');
  }
  db.appendRow(makeRow_(r,m,db.getLastColumn()));
  SpreadsheetApp.getUi().alert('Tire added successfully.'); clearForm();
}

function updateTire() {
  const {db} = sheets_(), r = readForm_(), m = map_(db), id = String(r.No).trim();
  if (!id) return SpreadsheetApp.getUi().alert('Enter or find a Tire No / ID in B5.');
  if (db.getLastRow() < 2) return SpreadsheetApp.getUi().alert('No records found.');
  const ids = db.getRange(2,m.No,db.getLastRow()-1,1).getDisplayValues().flat();
  const i = ids.findIndex(x => String(x).trim() === id);
  if (i < 0) return SpreadsheetApp.getUi().alert('Tire ID not found. Use Find Tire first.');
  db.getRange(i+2,1,1,db.getLastColumn()).setValues([makeRow_(r,m,db.getLastColumn())]);
  SpreadsheetApp.getUi().alert('Tire updated successfully.'); clearForm();
}

function clearForm() { sheets_().form.getRange('B5:B19').clearContent(); }
