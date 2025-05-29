package com.example.final_exam;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private IdiomDao idiomDao;
    private List<IdiomModel> currentIdioms;
    private int currentIdiomIndex = 0;
    private List<CharacterComponent> components;
    private Map<String, List<String>> characterComponentsMap;

    private GridLayout componentsGrid;
    private GridLayout targetGrid;
    private TextView idiomExplanation;
    private Button nextButton;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        idiomDao = new IdiomDao(this);
        componentsGrid = findViewById(R.id.components_grid);
        targetGrid = findViewById(R.id.target_grid);
        idiomExplanation = findViewById(R.id.idiom_explanation);
        nextButton = findViewById(R.id.next_button);
        backButton = findViewById(R.id.back_button);

        // 初始化部件映射表
        initCharacterComponentsMap();

        // 获取难度参数
        String level = getIntent().getStringExtra("level");
        if (level == null) {
            level = "primary"; // 默认难度
        }

        // 加载指定难度的成语
        loadIdioms(level);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNextIdiom();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 处理返回操作，启动选择难度界面
                Intent intent = new Intent(MainActivity.this, DifficultySelectionActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initCharacterComponentsMap() {
        characterComponentsMap = new HashMap<>();
        // 示例部件映射
        characterComponentsMap.put("明", Arrays.asList("日", "月"));
        characterComponentsMap.put("岩", Arrays.asList("山", "石"));
        characterComponentsMap.put("杯", Arrays.asList("木", "不"));
        characterComponentsMap.put("弓", Arrays.asList("弓"));
        characterComponentsMap.put("蛇", Arrays.asList("舌", "虫")); // 谐音替换
        characterComponentsMap.put("影", Arrays.asList("日", "京", "彡"));
        // 更多映射...
    }

    private void loadIdioms(String level) {
        currentIdioms = idiomDao.getRandomIdiomsByLevel(level, 5);
        if (!currentIdioms.isEmpty()) {
            loadCurrentIdiom();
        } else {
            Toast.makeText(this, "没有找到成语数据", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadCurrentIdiom() {
        IdiomModel currentIdiom = currentIdioms.get(currentIdiomIndex);
        idiomExplanation.setText(currentIdiom.getExplanation());

        // 解析成语为部件
        parseIdiomToComponents(currentIdiom.getIdiom());

        // 显示部件和目标位置
        displayComponents();
        displayTargetPositions(currentIdiom.getIdiom());
    }

    private void parseIdiomToComponents(String idiom) {
        components = new ArrayList<>();
        Random random = new Random();

        for (char c : idiom.toCharArray()) {
            String character = String.valueOf(c);
            if (characterComponentsMap.containsKey(character)) {
                List<String> charComponents = characterComponentsMap.get(character);
                for (String component : charComponents) {
                    components.add(new CharacterComponent(component, character));
                }
            } else {
                // 默认处理，将单字作为部件
                components.add(new CharacterComponent(character, character));
            }
        }

        // 添加一些干扰部件
        List<String> 干扰部件 = Arrays.asList("氵", "扌", "口", "心", "禾", "火", "土", "金");
        int 干扰数量 = Math.max(2, 8 - components.size());
        for (int i = 0; i < 干扰数量; i++) {
            int index = random.nextInt(干扰部件.size());
            components.add(new CharacterComponent(干扰部件.get(index), ""));
        }

        // 随机打乱部件顺序
        java.util.Collections.shuffle(components);
    }

    private void displayComponents() {
        componentsGrid.removeAllViews();
        componentsGrid.setColumnCount(4);

        for (final CharacterComponent component : components) {
            Button button = new Button(this);
            button.setText(component.getComponent());
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 处理部件点击事件
                    handleComponentClick(component, (Button) v);
                }
            });

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 120;
            params.height = 120;
            params.setMargins(10, 10, 10, 10);
            componentsGrid.addView(button, params);
        }
    }

    private void displayTargetPositions(String idiom) {
        targetGrid.removeAllViews();
        targetGrid.setColumnCount(4);

        for (int i = 0; i < idiom.length(); i++) {
            final int position = i;
            Button targetButton = new Button(this);
            targetButton.setText("");
            targetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 处理目标位置点击事件
                    handleTargetClick(position, (Button) v);
                }
            });

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 120;
            params.height = 120;
            params.setMargins(10, 10, 10, 10);
            targetGrid.addView(targetButton, params);
        }
    }

    private void handleComponentClick(CharacterComponent component, Button button) {
        // 处理部件点击事件
        if (component.isUsed()) {
            return;
        }

        // 查找第一个空的目标位置
        for (int i = 0; i < targetGrid.getChildCount(); i++) {
            Button targetButton = (Button) targetGrid.getChildAt(i);
            if (targetButton.getText().toString().isEmpty()) {
                targetButton.setText(component.getComponent());
                targetButton.setTag(component);
                component.setUsed(true);
                button.setEnabled(false);
                checkCompletion();
                return;
            }
        }
    }

    private void handleTargetClick(int position, Button targetButton) {
        // 处理目标位置点击事件
        if (targetButton.getTag() != null) {
            CharacterComponent component = (CharacterComponent) targetButton.getTag();
            component.setUsed(false);
            targetButton.setText("");
            targetButton.setTag(null);

            // 重新启用对应的部件按钮
            for (int i = 0; i < componentsGrid.getChildCount(); i++) {
                Button componentButton = (Button) componentsGrid.getChildAt(i);
                if (componentButton.getText().toString().equals(component.getComponent())) {
                    componentButton.setEnabled(true);
                    break;
                }
            }
        }
    }

    private void checkCompletion() {
        IdiomModel currentIdiom = currentIdioms.get(currentIdiomIndex);
        String idiom = currentIdiom.getIdiom();
        StringBuilder formedIdiom = new StringBuilder();

        // 收集当前已组成的成语
        for (int i = 0; i < targetGrid.getChildCount(); i++) {
            Button targetButton = (Button) targetGrid.getChildAt(i);
            if (targetButton.getTag() != null) {
                CharacterComponent component = (CharacterComponent) targetButton.getTag();
                formedIdiom.append(component.getTargetCharacter());
            }
        }

        // 检查是否组成正确的成语
        if (formedIdiom.toString().equals(idiom)) {
            Toast.makeText(this, "恭喜！你组成了成语：" + idiom, Toast.LENGTH_SHORT).show();
            nextButton.setEnabled(true);
        } else {
            nextButton.setEnabled(false);
        }
    }

    private void loadNextIdiom() {
        currentIdiomIndex = (currentIdiomIndex + 1) % currentIdioms.size();
        loadCurrentIdiom();
    }
}