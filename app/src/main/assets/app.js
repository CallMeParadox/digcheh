/**
 * دیگچه | Digcheh - موتور محاسبات و مدیریت وضعیت اپلیکیشن
 * پایش هوشمند متابولیسم، ماکروها، چیت‌میل و پایگاه داده محلی
 */

const STORAGE_KEY = 'digcheh_app_state_v1';

// Initial default state
const defaultState = {
  user: {
    name: 'کاربر عزیز',
    gender: 'male',
    height: 180,
    weight: 78,
    targetWeight: 75,
    activityLevel: 'moderate',
    goal: 'loss', // loss, maintain, gain
    currentDiet: {
      id: 'high-pro',
      name: 'پرپروتئین ورزشی',
      targetCal: 2200,
      targetP: 145,
      targetC: 210,
      targetF: 65,
      cheatInterval: 10
    }
  },
  cheatMeal: {
    intervalDays: 10,
    daysLeft: 3,
    bonusCal: 600
  },
  selectedDate: getTodayString(),
  logs: {} // date -> { meals: { breakfast: [], lunch: [], dinner: [], snack: [] }, exercises: [] }
};

// Current App State
let state = loadState();
let allFoods = [];
let targetMeal = 'breakfast';
let selectedFood = null;

function getTodayString() {
  const d = new Date();
  return d.toISOString().split('T')[0];
}

function loadState() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (raw) return JSON.parse(raw);
  } catch (e) {
    console.error('Failed to load state from localStorage', e);
  }
  return JSON.parse(JSON.stringify(defaultState));
}

function saveState() {
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(state));
  } catch (e) {
    console.error('Failed to save state to localStorage', e);
  }
}

// Get or initialize log for selected date
function getDayLog(dateStr) {
  if (!state.logs[dateStr]) {
    state.logs[dateStr] = {
      meals: {
        breakfast: [
          {
            id: 'init_1',
            foodId: 'sangak',
            name: 'نان سنگک سنتی کنجدی برشته',
            amount: 2,
            unitKey: 'palm',
            unitLabel: 'کف دست بدون انگشتان (۳۰g)',
            cal: 160,
            p: 6,
            c: 32,
            f: 1.5
          },
          {
            id: 'init_2',
            foodId: 'lighvan',
            name: 'پنیر لیقوان تبریز + گردو',
            amount: 1,
            unitKey: 'piece',
            unitLabel: 'قوطی کبریت (۳۰g)',
            cal: 230,
            p: 11,
            c: 2,
            f: 19.5
          }
        ],
        lunch: [
          {
            id: 'init_3',
            foodId: 'joojeh',
            name: 'چلو جوجه‌کباب زعفرانی',
            amount: 1,
            unitKey: 'plate',
            unitLabel: 'پرس کامل استاندارد',
            cal: 680,
            p: 56,
            c: 66,
            f: 19
          }
        ],
        dinner: [
          {
            id: 'init_4',
            foodId: 'omelet',
            name: 'املت سفیده تخم‌مرغ و قارچ',
            amount: 1,
            unitKey: 'plate',
            unitLabel: 'پرس تک‌نفره',
            cal: 340,
            p: 28,
            c: 36,
            f: 4
          }
        ],
        snack: [
          {
            id: 'init_5',
            foodId: 'apple',
            name: 'سیب قرمز دماوند + ۸ عدد بادام',
            amount: 1,
            unitKey: 'piece',
            unitLabel: 'یک عدد متوسط',
            cal: 140,
            p: 3,
            c: 19,
            f: 6
          }
        ]
      },
      exercises: [
        {
          id: 'ex_1',
          name: 'دویدن با سرعت متوسط (۹ کیلومتر بر ساعت)',
          duration: 30,
          burned: 260
        },
        {
          id: 'ex_2',
          name: 'تمرین با وزنه و بدنسازی',
          duration: 20,
          burned: 120
        }
      ]
    };
    saveState();
  }
  return state.logs[dateStr];
}

// Calculate Day Totals
function calculateDayStats(dateStr) {
  const dayLog = getDayLog(dateStr);
  let totalCal = 0;
  let totalP = 0;
  let totalC = 0;
  let totalF = 0;

  ['breakfast', 'lunch', 'dinner', 'snack'].forEach(meal => {
    (dayLog.meals[meal] || []).forEach(item => {
      totalCal += item.cal || 0;
      totalP += item.p || 0;
      totalC += item.c || 0;
      totalF += item.f || 0;
    });
  });

  let totalBurned = 0;
  (dayLog.exercises || []).forEach(ex => {
    totalBurned += ex.burned || 0;
  });

  const targetCal = state.user.currentDiet.targetCal || 2200;
  const remainingCal = Math.max(0, targetCal - totalCal);

  return {
    totalCal,
    totalP: Math.round(totalP),
    totalC: Math.round(totalC),
    totalF: Math.round(totalF),
    totalBurned,
    targetCal,
    remainingCal,
    percent: Math.min(100, Math.round((totalCal / targetCal) * 100))
  };
}

// Render Dashboard
function renderDashboard() {
  const stats = calculateDayStats(state.selectedDate);
  const dayLog = getDayLog(state.selectedDate);

  // Update Hero Energy Matrix
  document.getElementById('cal-remaining').innerText = stats.remainingCal.toLocaleString('fa-IR');
  document.getElementById('cal-eaten').innerText = stats.totalCal.toLocaleString('fa-IR');
  document.getElementById('cal-target').innerText = stats.targetCal.toLocaleString('fa-IR');
  document.getElementById('flow-bar').style.width = `${stats.percent}%`;
  document.getElementById('flow-percent').innerText = `${stats.percent}٪ پر شده`;

  // Update Macro Bentos
  document.getElementById('p-text').innerText = `${stats.totalP} / ${state.user.currentDiet.targetP}g`;
  document.getElementById('c-text').innerText = `${stats.totalC} / ${state.user.currentDiet.targetC}g`;
  document.getElementById('f-text').innerText = `${stats.totalF} / ${state.user.currentDiet.targetF}g`;

  const pRatio = Math.min(100, Math.round((stats.totalP / state.user.currentDiet.targetP) * 100));
  const cRatio = Math.min(100, Math.round((stats.totalC / state.user.currentDiet.targetC) * 100));
  const fRatio = Math.min(100, Math.round((stats.totalF / state.user.currentDiet.targetF) * 100));

  document.getElementById('p-bar-fill').style.width = `${pRatio}%`;
  document.getElementById('c-bar-fill').style.width = `${cRatio}%`;
  document.getElementById('f-bar-fill').style.width = `${fRatio}%`;

  // Update Diet Badge & Cheat Pill
  document.getElementById('diet-badge-text').innerText = state.user.currentDiet.name;
  document.getElementById('cheat-pill-text').innerText = `چیت‌میل: ${state.cheatMeal.daysLeft} روز دیگر`;

  // Render Meals
  renderMealList('breakfast', dayLog.meals.breakfast, 'bf-list', 'bf-total');
  renderMealList('lunch', dayLog.meals.lunch, 'ln-list', 'ln-total');
  renderMealList('dinner', dayLog.meals.dinner, 'dn-list', 'dn-total');
  renderMealList('snack', dayLog.meals.snack, 'sn-list', 'sn-total');

  // Render Exercises
  renderExerciseList(dayLog.exercises, stats.totalBurned);

  // Date Label
  updateDateLabel();
}

function renderMealList(mealKey, items, listId, totalId) {
  const container = document.getElementById(listId);
  const totalEl = document.getElementById(totalId);
  container.innerHTML = '';

  let mealSum = 0;
  items.forEach(item => {
    mealSum += item.cal;
    const div = document.createElement('div');
    div.className = 'py-3';
    div.id = `item_${item.id}`;
    div.innerHTML = `
      <div class="flex items-center justify-between text-xs cursor-pointer" onclick="toggleMacro('macro_${item.id}')">
        <div>
          <span class="font-extrabold text-slate-800">${item.name}</span>
          <span class="text-slate-400 text-[11px] block mt-0.5">(${item.amount} ${item.unitLabel})</span>
        </div>
        <div class="flex items-center gap-2">
          <span class="font-black text-slate-700 en-num">${item.cal} کالری</span>
          <svg class="w-3.5 h-3.5 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="m6 9 6 6 6-6"/></svg>
        </div>
      </div>
      <div id="macro_${item.id}" class="accordion-content pt-3">
        <div class="p-3 rounded-2xl bg-slate-50/80 border border-slate-100 flex items-center justify-between text-xs">
          <div class="flex gap-4">
            <div><span class="text-[10px] text-slate-400 block">پروتئین</span><span class="font-bold text-blue-600 en-num">${item.p}g</span></div>
            <div><span class="text-[10px] text-slate-400 block">کربوهیدرات</span><span class="font-bold text-amber-600 en-num">${item.c}g</span></div>
            <div><span class="text-[10px] text-slate-400 block">چربی</span><span class="font-bold text-rose-600 en-num">${item.f}g</span></div>
          </div>
          <button onclick="removeFoodItem('${mealKey}', '${item.id}')" class="text-rose-500 hover:text-rose-700 text-xs font-bold flex items-center gap-1 bg-white px-2.5 py-1 rounded-xl border border-rose-100 shadow-2xs">
            <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M3 6h18M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2M10 11v6M14 11v6"/></svg>
            <span>حذف</span>
          </button>
        </div>
      </div>
    `;
    container.appendChild(div);
  });

  totalEl.innerText = `${mealSum} kcal`;
}

function renderExerciseList(exercises, totalBurned) {
  const container = document.getElementById('exercise-list');
  document.getElementById('exercise-total-burn').innerText = `${totalBurned} کالری مصرف‌شده`;
  container.innerHTML = '';

  exercises.forEach(ex => {
    const div = document.createElement('div');
    div.className = 'p-3 rounded-2xl bg-slate-50 flex items-center justify-between text-xs';
    div.innerHTML = `
      <span class="font-bold text-slate-700">${ex.name}</span>
      <div class="flex items-center gap-2 text-slate-400 text-[11px]">
        <span>${ex.duration} دقیقه</span>
        <span class="font-black text-emerald-600 en-num">+${ex.burned} kcal</span>
      </div>
    `;
    container.appendChild(div);
  });
}

function removeFoodItem(mealKey, itemId) {
  const dayLog = getDayLog(state.selectedDate);
  dayLog.meals[mealKey] = dayLog.meals[mealKey].filter(it => it.id !== itemId);
  saveState();
  renderDashboard();
}

function toggleMacro(id) {
  const el = document.getElementById(id);
  if (el) el.classList.toggle('open');
}

// Food Modal & Units
function openFoodModal(meal) {
  targetMeal = meal;
  const names = { breakfast: 'صبحانه', lunch: 'ناهار', dinner: 'شام', snack: 'میان‌وعده' };
  document.getElementById('food-modal-title').innerText = `افزودن غذا به ${names[meal] || 'وعده'}`;
  document.getElementById('food-modal').classList.remove('hidden');
  document.getElementById('live-search-input').value = '';
  renderFoodList(allFoods);
  selectFoodFromList(allFoods[0]);
}

function closeFoodModal() {
  document.getElementById('food-modal').classList.add('hidden');
}

function renderFoodList(list) {
  const container = document.getElementById('food-selection-list');
  container.innerHTML = '';
  list.forEach(food => {
    const div = document.createElement('div');
    div.className = 'p-3 rounded-2xl bg-slate-50 hover:bg-emerald-50/50 border border-slate-100 cursor-pointer flex items-center justify-between transition';
    div.onclick = () => selectFoodFromList(food);
    div.innerHTML = `
      <div>
        <span class="text-xs font-extrabold text-slate-800 block">${food.name}</span>
        <span class="text-[10px] text-slate-400">${food.category} • ۱۰۰g: ${food.baseCalPer100g} kcal</span>
      </div>
      <span class="text-xs text-emerald-600 font-extrabold">انتخاب ›</span>
    `;
    container.appendChild(div);
  });
}

function selectFoodFromList(food) {
  if (!food) return;
  selectedFood = food;
  document.getElementById('selected-food-name').innerText = food.name;
  const unitSelect = document.getElementById('contextual-unit-select');
  unitSelect.innerHTML = '';

  food.units.forEach(u => {
    const opt = document.createElement('option');
    opt.value = u.key;
    opt.innerText = u.label;
    unitSelect.appendChild(opt);
  });

  document.getElementById('portion-amount').value = (food.units[0].key === 'g' ? 100 : (food.units[0].key === 'spoon' ? 5 : 1));
  recalcSelectedFood();
}

function recalcSelectedFood() {
  if (!selectedFood) return;
  const unitKey = document.getElementById('contextual-unit-select').value;
  const amount = parseFloat(document.getElementById('portion-amount').value) || 1;
  const unitObj = selectedFood.units.find(u => u.key === unitKey) || selectedFood.units[0];
  const cal = Math.round(unitObj.cal * amount);
  document.getElementById('selected-food-cal-preview').innerText = `${cal} کالری`;
}

function handleLiveFoodSearch() {
  const q = document.getElementById('live-search-input').value.trim().toLowerCase();
  if (!q) {
    renderFoodList(allFoods);
    return;
  }
  const filtered = allFoods.filter(f => f.name.toLowerCase().includes(q) || f.category.toLowerCase().includes(q));
  renderFoodList(filtered);
}

function confirmAddFoodToList() {
  if (!selectedFood) return;
  const unitKey = document.getElementById('contextual-unit-select').value;
  const unitObj = selectedFood.units.find(u => u.key === unitKey) || selectedFood.units[0];
  const amount = parseFloat(document.getElementById('portion-amount').value) || 1;
  const cal = Math.round(unitObj.cal * amount);

  const grams = amount * unitObj.grams;
  const p = Math.round(selectedFood.p * (grams / 100));
  const c = Math.round(selectedFood.c * (grams / 100));
  const f = Math.round(selectedFood.f * (grams / 100));

  const dayLog = getDayLog(state.selectedDate);
  dayLog.meals[targetMeal].push({
    id: `item_${Date.now()}`,
    foodId: selectedFood.id,
    name: selectedFood.name,
    amount,
    unitKey,
    unitLabel: unitObj.label.split(' (')[0],
    cal,
    p,
    c,
    f
  });

  saveState();
  renderDashboard();
  closeFoodModal();
}

// Cheat Meal Settings
function openCheatModal() {
  document.getElementById('cheat-modal').classList.remove('hidden');
  document.getElementById('cheat-days-input').value = state.cheatMeal.daysLeft;
  document.getElementById('cheat-select').value = state.cheatMeal.intervalDays;
}

function closeCheatModal() {
  document.getElementById('cheat-modal').classList.add('hidden');
}

function saveCheatModal() {
  state.cheatMeal.daysLeft = parseInt(document.getElementById('cheat-days-input').value) || 3;
  state.cheatMeal.intervalDays = parseInt(document.getElementById('cheat-select').value) || 10;
  saveState();
  renderDashboard();
  closeCheatModal();
}

// Diets & Profile
function openOnboardingModal() {
  document.getElementById('onboarding-modal').classList.remove('hidden');
}

function closeOnboardingModal() {
  document.getElementById('onboarding-modal').classList.add('hidden');
}

function selectDiet(id, name, targetCal, p, c, f, cheatDays) {
  state.user.currentDiet = { id, name, targetCal, targetP: p, targetC: c, targetF: f, cheatInterval: cheatDays };
  state.cheatMeal.intervalDays = cheatDays;
  state.cheatMeal.daysLeft = Math.min(state.cheatMeal.daysLeft, cheatDays);
  saveState();
  renderDashboard();
  closeOnboardingModal();
}

// Exercise
function openExerciseModal() {
  document.getElementById('exercise-modal').classList.remove('hidden');
}

function closeExerciseModal() {
  document.getElementById('exercise-modal').classList.add('hidden');
}

function confirmAddExercise() {
  const select = document.getElementById('exercise-select');
  const met = parseFloat(select.value);
  const exName = select.options[select.selectedIndex].text.split(' - ')[0];
  const duration = parseInt(document.getElementById('exercise-duration').value) || 30;
  const weight = state.user.weight || 78;

  // Formula: Burned = (MET * 3.5 * weight / 200) * minutes
  const burned = Math.round((met * 3.5 * weight / 200) * duration);

  const dayLog = getDayLog(state.selectedDate);
  dayLog.exercises.push({
    id: `ex_${Date.now()}`,
    name: exName,
    duration,
    burned
  });

  saveState();
  renderDashboard();
  closeExerciseModal();
}

// Date Travel
function changeDay(delta) {
  const current = new Date(state.selectedDate);
  current.setDate(current.getDate() + delta);
  state.selectedDate = current.toISOString().split('T')[0];
  renderDashboard();
}

function goToToday() {
  state.selectedDate = getTodayString();
  renderDashboard();
}

function updateDateLabel() {
  const todayStr = getTodayString();
  const banner = document.getElementById('past-banner');
  const label = document.getElementById('date-label');

  if (state.selectedDate === todayStr) {
    banner.classList.add('hidden');
    label.innerText = 'امروز، ۲ مهر';
  } else {
    banner.classList.remove('hidden');
    label.innerText = state.selectedDate;
  }
}

// Initialize on Load
window.addEventListener('DOMContentLoaded', async () => {
  try {
    const res = await fetch('./foods.json');
    allFoods = await res.json();
  } catch (e) {
    console.warn('Fallback foods', e);
  }
  renderDashboard();
});
