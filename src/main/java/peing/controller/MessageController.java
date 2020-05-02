package peing.controller;

import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import peing.pojo.*;
import peing.service.MessageService;
import peing.vo.PostMessageVo;
import javax.validation.Valid;
import java.util.Date;

/**
 * 消息系统的接口，前端未实现
 */
@RestController
@RequestMapping("/message")
public class MessageController {
    @Autowired
    private CurrentUser currentUser;
    @Autowired
    private MessageService messageService;

    @GetMapping("/getAll")
    public ResponseJson<PageResult<Message>> getAllMessage(@RequestParam(defaultValue = "1")int pageNum,
                                                           @RequestParam(defaultValue = "8")int pageSize){
        if(pageNum<=0||pageSize<=0||pageSize>50){
            return new ResponseJson<>(ResultCode.UNVALIDPARAMS);
        }
        Long userId = Long.parseLong(currentUser.getCurrentUser().getUsername());
        Page<Message> messagePage = messageService.selectMessageByUserId(userId,pageNum,pageSize);
        return new ResponseJson<>(ResultCode.SUCCESS,new PageResult<>(messagePage));
    }

    @PostMapping("/postMessage")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_ROOT')")
    public ResponseJson postMessage(@RequestBody @Valid PostMessageVo postMessageVo){
        String title = postMessageVo.getTitle();
        String content = postMessageVo.getContent();
        Message message = new Message();
        message.setTitle(title);
        message.setContent(content);
        message.setPublishDate(new Date());
        DefaultIdentifierGenerator identifierGenerator = new DefaultIdentifierGenerator(1,1);
        message.setMessageId(identifierGenerator.nextId(message));
        messageService.publishAnnouncement(message);
        return new ResponseJson(ResultCode.SUCCESS);
    }

    @GetMapping("getMessageInfo")
    public ResponseJson<Message> getMessageInfo(Long messageId){
        Long userId = Long.parseLong(currentUser.getCurrentUser().getUsername());
        messageService.setIsRead(userId,messageId);
        Message message = messageService.selectByMessageId(userId,messageId);
        return new ResponseJson<>(ResultCode.SUCCESS,message);
    }

    @GetMapping("getNum")
    public ResponseJson<Integer> getNum(){
        Long userId = Long.parseLong(currentUser.getCurrentUser().getUsername());
        Integer count = messageService.countUnreadMessage(userId);
        return new ResponseJson<>(ResultCode.SUCCESS,count);
    }
}
