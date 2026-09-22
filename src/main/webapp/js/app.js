// Todo List API Client
const API_BASE = '/api/todos';

// Загрузка всех задач при старте
document.addEventListener('DOMContentLoaded', () => {
    loadTodos('all');
});

// Загрузка задач
async function loadTodos(filter = 'all') {
    const todosList = document.getElementById('todosList');
    todosList.innerHTML = '<p>Загрузка...</p>';
    
    try {
        let url;
        if (filter === 'all') {
            url = API_BASE;
        } else {
            url = `${API_BASE}/status/${filter}`;
        }
        
        const response = await fetch(url);
        const todos = await response.json();
        
        if (todos.length === 0) {
            todosList.innerHTML = '<p style="text-align: center; color: #666;">Нет задач</p>';
            return;
        }
        
        renderTodos(todos);
    } catch (error) {
        console.error('Ошибка загрузки:', error);
        todosList.innerHTML = '<p style="color: red;">Ошибка загрузки данных</p>';
    }
}

// Рендеринг задач
function renderTodos(todos) {
    const todosList = document.getElementById('todosList');
    let html = '<ul class="todos-list">';
    
    todos.forEach(todo => {
        const statusClass = todo.status.toLowerCase().replace('_', '-');
        html += `
            <li class="todo-item ${statusClass}">
                <div>
                    <h3>${escapeHtml(todo.title)}</h3>
                    <p>${escapeHtml(todo.description || 'Нет описания')}</p>
                </div>
                <div class="todo-actions">
                    <button class="btn-complete" onclick="toggleStatus(${todo.id})">
                        ${todo.status === 'COMPLETED' ? 'Отменить' : 'Выполнить'}
                    </button>
                    <button class="btn-delete" onclick="deleteTodo(${todo.id})">Удалить</button>
                </div>
            </li>
        `;
    });
    
    html += '</ul>';
    todosList.innerHTML = html;
}

// Создание задачи
document.getElementById('createForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const title = document.getElementById('title').value;
    const description = document.getElementById('description').value;
    
    try {
        const response = await fetch(API_BASE, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ title, description })
        });
        
        if (response.ok) {
            document.getElementById('createForm').reset();
            loadTodos('all');
        } else {
            alert('Ошибка создания задачи');
        }
    } catch (error) {
        console.error('Ошибка:', error);
        alert('Ошибка сети');
    }
});

// Смена статуса задачи
async function toggleStatus(id) {
    try {
        const response = await fetch(`${API_BASE}/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                title: '', // оставим как есть
                description: '',
                status: 'COMPLETED'
            })
        });
        
        if (response.ok) {
            loadTodos('all');
        }
    } catch (error) {
        console.error('Ошибка:', error);
    }
}

// Удаление задачи
async function deleteTodo(id) {
    if (!confirm('Удалить эту задачу?')) return;
    
    try {
        const response = await fetch(`${API_BASE}/${id}`, {
            method: 'DELETE'
        });
        
        if (response.ok) {
            loadTodos('all');
        }
    } catch (error) {
        console.error('Ошибка:', error);
    }
}

// Фильтрация
function filterTodos(status) {
    loadTodos(status);
}

// Экранирование HTML
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}
