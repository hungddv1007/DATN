import { useCallback, useEffect, useState } from 'react';
import MainLayout from '../../components/layout/MainLayout';
import saleService from '../../services/saleService';
import './SaleDashboardPage.css';

const money = v => new Intl.NumberFormat('vi-VN').format(v || 0) + ' ₫';
const SaleDashboardPage = () => {
  const [dashboard, setDashboard] = useState(null); const [codes, setCodes] = useState([]);
  const [commissions, setCommissions] = useState([]); const [chats, setChats] = useState([]);
  const [selected, setSelected] = useState(null); const [messages, setMessages] = useState([]);
  const [message, setMessage] = useState(''); const [error, setError] = useState('');
  const [editingCodeId, setEditingCodeId] = useState(null);
  const [editCode, setEditCode] = useState({code:'',description:'',oneTimePerMember:true,expiresAt:''});
  const load = useCallback(async () => {
    const [d,c,m,h] = await Promise.all([saleService.dashboard(),saleService.codes(),saleService.commissions(),saleService.chats()]);
    setDashboard(d);setCodes(c);setCommissions(m);setChats(h);
  }, []);
  useEffect(()=>{
    load().catch(e=>setError(e.response?.data?.message||'Không thể tải dữ liệu'));
    const timer = window.setInterval(() => load().catch(() => {}), 5000);
    return () => window.clearInterval(timer);
  },[load]);
  useEffect(() => {
    if (!selected?.id) return undefined;
    const refresh = () => saleService.messages(selected.id).then(setMessages).catch(() => {});
    const timer = window.setInterval(refresh, 3000);
    return () => window.clearInterval(timer);
  }, [selected?.id]);
  const openChat=async c=>{setSelected(c);setMessages(await saleService.messages(c.id));};
  const send=async e=>{e.preventDefault();if(!message.trim())return;await saleService.sendMessage(selected.id,message);setMessage('');setMessages(await saleService.messages(selected.id));};
  const toggleAvailability=async()=>{
    const goingOnline=!dashboard.online;
    try {
      const updated=await saleService.setOnline(goingOnline);
      setDashboard(updated);
      if(!goingOnline){setSelected(null);setMessages([]);}
      await load();
    } catch(e) {
      setError(e.response?.data?.message||'Không thể cập nhật trạng thái');
    }
  };
  const beginEditCode=c=>{
    setEditingCodeId(c.id);
    setEditCode({
      code:c.code,
      description:c.description||'',
      oneTimePerMember:Boolean(c.oneTimePerMember),
      expiresAt:c.expiresAt?c.expiresAt.slice(0,16):'',
    });
  };
  const saveEditedCode=async e=>{
    e.preventDefault();
    setError('');
    try {
      await saleService.updateCode(editingCodeId,{
        ...editCode,
        code:editCode.code.trim().toUpperCase(),
        description:editCode.description.trim()||null,
        expiresAt:editCode.expiresAt||null,
      });
      setEditingCodeId(null);
      await load();
    } catch(e) {
      setError(e.response?.data?.message||'Không thể cập nhật mã giới thiệu');
    }
  };
  const toggleCodeStorage=async(c,archived)=>{
    setError('');
    try {
      await saleService.setCodeActive(c.id,!archived);
      await load();
    } catch(e) {
      setError(e.response?.data?.message||'Không thể cập nhật trạng thái mã giới thiệu');
    }
  };
  if(!dashboard)return <MainLayout><div className="sale-page">Đang tải...</div></MainLayout>;
  return <MainLayout><div className="sale-page"><h1>Trung Tâm Nhân Viên Sale</h1>{error&&<p className="sale-error">{error}</p>}
    <div className="sale-cards"><article><b>Cấp độ</b><strong>{dashboard.level}</strong><span>{dashboard.successfulCustomers} khách thành công</span></article>
      <article><b>Ưu đãi / Hoa hồng</b><strong>{dashboard.discountPercent}% / {dashboard.commissionRate}%</strong></article>
      <article className="commission-summary-card">
        <b>Hoa hồng</b>
        <div className="commission-summary-row pending">
          <span>Đang chờ</span>
          <strong>{money(dashboard.pendingCommission)}</strong>
        </div>
        <div className="commission-summary-row paid">
          <span>Đã nhận</span>
          <strong>{money(dashboard.paidCommission)}</strong>
        </div>
      </article>
      <article><b>Tư vấn đang mở</b><strong>{dashboard.activeChats}/3</strong></article></div>
    <button
      className={`sale-availability ${dashboard.online?'online':'offline'}`}
      onClick={toggleAvailability}
      aria-pressed={dashboard.online}
      title={dashboard.online?'Nhấn để chuyển sang ngoại tuyến và kết thúc mọi phiên tư vấn':'Nhấn để bắt đầu nhận tư vấn'}
    >
      {dashboard.online?'Trạng thái: Trực tuyến':'Trạng thái: Ngoại tuyến'}
    </button>
    <div className="sale-grid"><section><h2>Mã giới thiệu ({codes.length}/3)</h2>
      {codes.map(c=>editingCodeId===c.id?(
        <form className="sale-code-edit" key={c.id} onSubmit={saveEditedCode}>
          <label>Mã giới thiệu<input required minLength={4} maxLength={50} value={editCode.code} onChange={e=>setEditCode(current=>({...current,code:e.target.value.toUpperCase()}))}/></label>
          <label>Mô tả<input maxLength={255} value={editCode.description} onChange={e=>setEditCode(current=>({...current,description:e.target.value}))}/></label>
          <label>Hạn sử dụng<input type="datetime-local" value={editCode.expiresAt} onChange={e=>setEditCode(current=>({...current,expiresAt:e.target.value}))}/></label>
          <label className="sale-code-checkbox"><input type="checkbox" checked={editCode.oneTimePerMember} onChange={e=>setEditCode(current=>({...current,oneTimePerMember:e.target.checked}))}/> Mỗi member chỉ được dùng một lần</label>
          <div className="sale-code-actions"><button type="button" className="secondary" onClick={()=>setEditingCodeId(null)}>Hủy</button><button type="submit">Lưu thay đổi</button></div>
        </form>
      ):(
        <div className={`sale-row sale-code-row ${c.active?'':'archived'}`} key={c.id}>
          <div><b>{c.code}</b>{c.description&&<small>{c.description}</small>}</div>
          <span>-{c.discountPercent}% · {c.oneTimePerMember?'mỗi member 1 lần':'dùng nhiều lần'} · {c.active?'đang sử dụng':'đã lưu trữ'}</span>
          <div className="sale-code-actions">
            <button type="button" className="secondary" onClick={()=>beginEditCode(c)}>Chỉnh sửa</button>
            <label className="sale-code-storage-toggle">
              <input type="checkbox" checked={!c.active} onChange={e=>toggleCodeStorage(c,e.target.checked)}/>
              <span className="sale-code-switch" aria-hidden="true"></span>
              <span>Lưu trữ</span>
            </label>
          </div>
        </div>
      ))}</section>
      <section><h2>Tư vấn trực tiếp</h2><button onClick={()=>saleService.claimNext().then(load).catch(e=>setError(e.response?.data?.message))}>Nhận khách chờ lâu nhất</button>
        {chats.map(c=><button className="chat-pick" key={c.id} onClick={()=>openChat(c)}>{c.title} · {c.handoffStatus}</button>)}</section></div>
    {selected&&<section className="sale-chat"><div className="sale-chat-head"><h2>{selected.title}</h2><button onClick={()=>saleService.closeChat(selected.id).then(()=>{setSelected(null);load();})}>Kết thúc tư vấn</button></div>
      <div className="sale-messages">{messages.map(m=><p key={m.id} className={m.role==='SALE'?'mine':''}><b>{m.senderName||m.role}:</b> {m.content}</p>)}</div>
      <form onSubmit={send}><input value={message} onChange={e=>setMessage(e.target.value)} placeholder="Nhập nội dung tư vấn..."/><button>Gửi</button></form></section>}
    <section><h2>Hoa hồng</h2>{commissions.map(c=><div className="sale-row" key={c.id}><span>GD #{c.transactionId} · {c.memberName}</span><b>{money(c.commissionAmount)} · {c.status}</b></div>)}</section>
  </div></MainLayout>;
};
export default SaleDashboardPage;
