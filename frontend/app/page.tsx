'use client';

import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { applyPollResults, isTerminalTask, type Task } from '../lib/task-state';

const API = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api';
const MEDIA_ORIGIN = API.replace(/\/api\/?$/, '');
const userProfiles = [
  { id: 'demo-user', label: '演示用户' },
  { id: 'creator-lin', label: '创作者 林' },
  { id: 'creator-mia', label: '创作者 Mia' },
];
type Template = {
  id: string;
  title: string;
  category: string;
  coverUrl: string;
  description: string;
  requiredImageCount: number;
  acceptedMimeTypes: string;
  maxFileSizeBytes: number;
};
const statusLabels: Record<string, string> = {
  QUEUED: '等待处理', PROCESSING: '处理中', SUCCEEDED: '制作成功', FAILED: '制作失败',
};
const stageLabels: Record<string, string> = {
  ASSET_CHECK: '素材检查', GENERATING: '模拟生成', PACKAGING: '结果整理', COMPLETED: '已完成',
};
const byteLabel = (bytes: number) => `${Math.round(bytes / 1024 / 1024)} MB`;
const mediaUrl = (url: string) => url.startsWith('/results/') || url.startsWith('/uploads/') ? `${MEDIA_ORIGIN}${url}` : url;

export default function Home() {
  const [templates, setTemplates] = useState<Template[]>([]);
  const [category, setCategory] = useState('全部模板');
  const [selected, setSelected] = useState<Template | null>(null);
  const [files, setFiles] = useState<(File | null)[]>([]);
  const [activeSlot, setActiveSlot] = useState(0);
  const [tasks, setTasks] = useState<Task[]>([]);
  const [error, setError] = useState('');
  const [pollingError, setPollingError] = useState(false);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [preview, setPreview] = useState<{ src: string; title: string } | null>(null);
  const [userId, setUserId] = useState('demo-user');
  const [identityReady, setIdentityReady] = useState(false);
  const [accountOpen, setAccountOpen] = useState(false);
  const input = useRef<HTMLInputElement>(null);
  const loadController = useRef<AbortController | null>(null);

  useEffect(() => {
    const savedUser = window.localStorage.getItem('material-studio-user');
    if (savedUser && userProfiles.some(profile => profile.id === savedUser)) setUserId(savedUser);
    setIdentityReady(true);
  }, []);

  const load = useCallback(async (quiet = false) => {
    if (!identityReady) return;
    loadController.current?.abort();
    const controller = new AbortController();
    loadController.current = controller;
    if (!quiet) setRefreshing(true);
    try {
      const [templateResponse, taskResponse] = await Promise.all([
        fetch(`${API}/templates`, { signal: controller.signal }),
        fetch(`${API}/tasks`, { headers: { 'X-Demo-User': userId }, signal: controller.signal }),
      ]);
      if (!templateResponse.ok || !taskResponse.ok) throw new Error('服务暂时不可用');
      setTemplates(await templateResponse.json());
      setTasks(await taskResponse.json());
      setError('');
      setPollingError(false);
    } catch (cause) {
      if (cause instanceof DOMException && cause.name === 'AbortError') return;
      setError('暂时无法连接服务。已有任务状态已保留，请稍后重试。');
    } finally {
      if (!controller.signal.aborted) {
        setLoading(false);
        setRefreshing(false);
      }
    }
  }, [identityReady, userId]);

  useEffect(() => {
    if (!identityReady) return;
    setTasks([]);
    void load(true);
    return () => loadController.current?.abort();
  }, [identityReady, userId, load]);

  useEffect(() => {
    const activeTasks = tasks.filter(task => !isTerminalTask(task));
    if (!identityReady || activeTasks.length === 0) return;
    const controller = new AbortController();
    const timer = window.setInterval(async () => {
      const results = await Promise.all(activeTasks.map(async task => {
        try {
          const response = await fetch(`${API}/tasks/${task.id}`, { headers: { 'X-Demo-User': userId }, signal: controller.signal });
          if (!response.ok) return { id: task.id, requestFailed: true };
          return { id: task.id, task: await response.json() as Task, requestFailed: false };
        } catch {
          return { id: task.id, requestFailed: true };
        }
      }));
      if (controller.signal.aborted) return;
      setTasks(current => applyPollResults(current, results).tasks);
      setPollingError(results.some(result => result.requestFailed));
    }, 1400);
    return () => { controller.abort(); window.clearInterval(timer); };
  }, [tasks, userId, identityReady]);

  const categories = useMemo(() => ['全部模板', ...new Set(templates.map(template => template.category))], [templates]);
  const visibleTemplates = templates.filter(template => category === '全部模板' || template.category === category);
  const featured = templates.find(template => template.id === 'demo-postcard') || templates[0];
  const localPreviews = useMemo(() => files.map(file => file ? URL.createObjectURL(file) : null), [files]);
  useEffect(() => () => localPreviews.forEach(url => { if (url) URL.revokeObjectURL(url); }), [localPreviews]);

  useEffect(() => {
    if (!selected && !preview && !accountOpen) return;
    const onKeyDown = (event: KeyboardEvent) => {
      if (event.key === 'Escape') { setSelected(null); setPreview(null); setAccountOpen(false); }
    };
    window.addEventListener('keydown', onKeyDown);
    return () => window.removeEventListener('keydown', onKeyDown);
  }, [selected, preview, accountOpen]);

  const currentProfile = userProfiles.find(profile => profile.id === userId) || userProfiles[0];

  function switchUser(nextUser: string) {
    window.localStorage.setItem('material-studio-user', nextUser);
    setAccountOpen(false);
    setSelected(null);
    setPreview(null);
    setPollingError(false);
    setError('');
    setTasks([]);
    setUserId(nextUser);
  }

  function openTemplate(template: Template) {
    setSelected(template);
    setFiles(Array(template.requiredImageCount).fill(null));
    setActiveSlot(0);
    setError('');
  }

  function selectFiles(list: FileList | null) {
    if (!selected) return;
    const file = list?.[0];
    if (!file) return;
    const acceptedTypes = selected.acceptedMimeTypes.split(',');
    if (!acceptedTypes.includes(file.type) || file.size > selected.maxFileSizeBytes) {
      setError(`请使用 ${acceptedTypes.map(type => type.replace('image/', '').toUpperCase()).join(' / ')} 图片，单张不超过 ${byteLabel(selected.maxFileSizeBytes)}。`);
      return;
    }
    setFiles(current => current.map((currentFile, index) => index === activeSlot ? file : currentFile));
    setError('');
  }

  function removeFile(index: number) {
    setFiles(current => current.map((file, fileIndex) => fileIndex === index ? null : file));
  }

  async function submitTask() {
    if (!selected || files.some(file => !file)) return;
    setSubmitting(true);
    setError('');
    const form = new FormData();
    form.append('templateId', selected.id);
    files.forEach(file => { if (file) form.append('files', file); });
    try {
      const response = await fetch(`${API}/tasks`, { method: 'POST', headers: { 'X-Demo-User': userId }, body: form });
      const body = await response.json();
      if (!response.ok) throw new Error(body.message || '提交失败，请检查素材后重试。');
      setTasks(current => [body, ...current]);
      setSelected(null);
      setFiles([]);
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : '提交失败，请重试。');
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <main className="studio">
      <header className="topbar">
        <a className="brand" href="#top" aria-label="灵感工坊首页">
          <span className="brand-mark">L</span>
          <span>灵感工坊</span>
        </a>
        <nav className="top-nav" aria-label="主导航">
          <a className="nav-active" href="#templates">发现模板</a>
          <a href="#history">制作记录</a>
        </nav>
        <div className="account-wrap">
          <button className="account" aria-expanded={accountOpen} aria-haspopup="menu" onClick={() => setAccountOpen(open => !open)}><span className="account-dot" />{currentProfile.label} <strong>{currentProfile.id}</strong><span aria-hidden="true">⌄</span></button>
          {accountOpen && <div className="account-menu" role="menu" aria-label="切换创作者身份">
            <span>本地创作者档案</span>
            {userProfiles.map(profile => <button key={profile.id} role="menuitem" className={profile.id === userId ? 'active' : ''} onClick={() => switchUser(profile.id)}><span>{profile.label}</span><small>{profile.id}</small>{profile.id === userId && <b aria-hidden="true">✓</b>}</button>)}
            <p>身份保存在当前浏览器，每个档案只显示自己的制作记录。</p>
          </div>}
        </div>
      </header>

      {error && <div className="notice error" role="alert"><span>{error}</span><button onClick={() => void load()}>重新连接</button></div>}
      {pollingError && <div className="notice warning" role="status"><span>状态查询暂时失败，任务不会因此变成失败。</span><button onClick={() => void load()}>恢复查询</button></div>}

      <section className="feature" id="top" aria-label="精选模板">
        {featured && <img className="feature-image" src={featured.coverUrl} alt="" />}
        <div className="feature-shade" />
        <div className="feature-copy">
          <span className="feature-kicker">精选创作 · FEATURED TEMPLATE</span>
          <h1>{featured?.title || '灵感工坊'}</h1>
          <p>{featured?.description || '选择模板，开始制作。'}</p>
          <div className="feature-meta">
            {featured && <><span>{featured.category}</span><i /> <span>{featured.requiredImageCount} 张图片</span><i /> <span>模拟生成</span></>}
          </div>
          {featured && <button className="feature-action" onClick={() => openTemplate(featured)}>使用此模板 <span aria-hidden="true">↗</span></button>}
        </div>
        <span className="feature-index">01 <i /> 03</span>
      </section>

      <section className="template-section" id="templates">
        <div className="section-topline"><span className="eyebrow">TEMPLATE LIBRARY</span><span className="result-count">{visibleTemplates.length} 个创作方向</span></div>
        <div className="section-heading">
          <div><h2>从一张图片开始</h2><p>选择创作方向，上传素材，随时回来查看结果。</p></div>
          <div className="filters" role="group" aria-label="按分类筛选模板">
            {categories.map(item => <button key={item} className={category === item ? 'filter selected' : 'filter'} aria-pressed={category === item} onClick={() => setCategory(item)}>{item}</button>)}
          </div>
        </div>

        {loading ? <div className="empty">正在读取模板…</div> : visibleTemplates.length === 0 ? <div className="empty">这个分类暂时没有模板。</div> : (
          <div className="template-grid">
            {visibleTemplates.map((template, index) => (
              <button className="template-card" key={template.id} onClick={() => openTemplate(template)} aria-label={`查看并制作：${template.title}`}>
                <span className="template-image-wrap"><img src={template.coverUrl} alt=""/><span className="template-open">打开模板 <b aria-hidden="true">↗</b></span><span className="template-number">0{index + 1}</span></span>
                <span className="template-details"><span className="template-category">{template.category}</span><strong>{template.title}</strong><span className="template-description">{template.description}</span><span className="template-spec">{template.requiredImageCount} 张图片 <i /> {template.acceptedMimeTypes.split(',').map(type => type.replace('image/', '').toUpperCase()).join(' / ')}</span></span>
              </button>
            ))}
          </div>
        )}
      </section>

      <section className="history-section" id="history">
        <div className="history-heading"><div><span className="eyebrow">YOUR CREATIONS</span><h2>制作记录</h2><p className="identity-note">当前档案：{currentProfile.label} · 刷新后仍会恢复此身份的记录</p></div><button className="refresh-button" disabled={refreshing} onClick={() => void load()}><span aria-hidden="true">↻</span>{refreshing ? '正在刷新' : '刷新记录'}</button></div>
        {tasks.length === 0 ? <div className="history-empty">{currentProfile.label} 还没有制作任务。选一个模板，开始第一次创作。</div> : (
          <div className="history-list">
            {tasks.map(task => {
              const template = templates.find(item => item.id === task.templateId);
              return <article className="history-row" key={task.id}>
                <div className="history-thumb">{task.status === 'SUCCEEDED' && task.resultUrl ? <button aria-label={`预览${template?.title || '制作'}结果`} onClick={() => setPreview({ src: mediaUrl(task.resultUrl!), title: `${template?.title || '制作'}结果` })}><img src={mediaUrl(task.resultUrl)} alt="" /></button> : <span>{template?.category || '制作中'}</span>}</div>
                <div className="history-info"><strong>{template?.title || task.templateId}</strong><span>{new Date(task.createdAt).toLocaleString('zh-CN')}</span></div>
                <div className="history-stage"><span className={`task-status ${task.status.toLowerCase()}`}>{statusLabels[task.status] || task.status}</span><span>{stageLabels[task.stage] || task.stage}</span></div>
                {task.status === 'FAILED' && <p className="task-error">{task.errorMessage || '制作失败'}</p>}
                {task.status === 'SUCCEEDED' && task.resultUrl && <button className="view-result" onClick={() => setPreview({ src: mediaUrl(task.resultUrl!), title: `${template?.title || '制作'}结果` })}>查看结果 <span aria-hidden="true">↗</span></button>}
              </article>;
            })}
          </div>
        )}
      </section>

      <footer className="footer"><span>灵感工坊 · 异步素材制作演示</span><span>本地模拟生成，不调用真实 AI</span></footer>

      {selected && (
        <div className="modal-backdrop" onClick={() => setSelected(null)}>
          <section className="modal" role="dialog" aria-modal="true" aria-labelledby="dialog-title" onClick={event => event.stopPropagation()}>
            <button className="close" aria-label="关闭" onClick={() => setSelected(null)}>×</button>
            <div className="detail-layout">
              <div className="detail-cover"><img src={selected.coverUrl} alt={`${selected.title}模板示例`} /></div>
              <div className="detail-copy"><span className="eyebrow">{selected.category}</span><h2 id="dialog-title">{selected.title}</h2><p>{selected.description}</p>
                <div className="requirements"><span>素材要求</span><strong>{selected.requiredImageCount} 张图片</strong><span>{selected.acceptedMimeTypes.split(',').map(type => type.replace('image/', '').toUpperCase()).join(' / ')}</span><span>单张最大 {byteLabel(selected.maxFileSizeBytes)}</span></div>
              </div>
            </div>
            <div className="upload-heading"><div><strong>添加你的图片</strong><span>{files.filter(Boolean).length} / {selected.requiredImageCount} 张已就绪</span></div><span>选择或替换图片</span></div>
            <div className="upload-grid">{Array.from({ length: selected.requiredImageCount }).map((_, index) => (
              <div className="upload-item" key={index}><button className={files[index] ? 'upload-slot has-file' : 'upload-slot'} onClick={() => { setActiveSlot(index); input.current?.click(); }} aria-label={files[index] ? `替换第 ${index + 1} 张图片` : `上传第 ${index + 1} 张图片`}>
                {localPreviews[index] ? <><img src={localPreviews[index]!} alt={`第 ${index + 1} 张素材预览`} /><span className="replace-label">替换图片</span></> : <><span className="upload-plus">+</span><span>图片 {index + 1}</span></>}
              </button>{files[index] && <button className="remove" onClick={() => removeFile(index)}>移除图片</button>}</div>
            ))}</div>
            <input ref={input} hidden type="file" accept={selected.acceptedMimeTypes} onChange={event => { selectFiles(event.target.files); event.currentTarget.value = ''; }} />
            {error && <p className="dialog-error" role="alert">{error}</p>}
            <div className="modal-footer"><span>任务受理后可在记录中继续查看进度</span><button className="primary" disabled={submitting || files.some(file => !file)} onClick={() => void submitTask()}>{submitting ? '正在受理…' : '提交制作任务'} <span aria-hidden="true">↗</span></button></div>
          </section>
        </div>
      )}

      {preview && <div className="preview-backdrop" onClick={() => setPreview(null)}><section className="preview-dialog" role="dialog" aria-modal="true" aria-label={preview.title} onClick={event => event.stopPropagation()}><button className="close" aria-label="关闭预览" onClick={() => setPreview(null)}>×</button><img src={preview.src} alt={preview.title}/><span>{preview.title}</span></section></div>}
    </main>
  );
}
